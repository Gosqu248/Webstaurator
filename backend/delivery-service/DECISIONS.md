# Decyzje architektoniczne — delivery-service

## Hybryda SSE + Kafka dla `delivery.location`

`DeliveryTrackingService` (metoda aktualizująca lokalizację kuriera) robi dwa
zapisy dla tego samego zdarzenia:

1. `deliveryLocationPublisher.publish(...)` → Kafka, topik `delivery.location`
   (6 partycji, klucz = `orderId`).
2. `broadcaster.publish(orderId, ...)` → `DeliveryTrackingBroadcaster`, czyli
   bezpośredni push do `SseEmitter` trzymanych w pamięci procesu
   (`DeliveryTrackingSseController`).

To celowe, nie duplikacja do posprzątania:

- **SSE** obsługuje ścieżkę "klient śledzi zamówienie na żywo" — wymaga
  najmniejszego możliwego opóźnienia (pojedyncza aktualizacja lokalizacji co
  kilka sekund), a klient i tak jest podłączony do konkretnej instancji
  delivery-service przez to samo połączenie HTTP. Przechodzenie przez Kafkę
  po drodze do własnego SSE emittera dodałoby tylko opóźnienie i punkt awarii
  bez żadnej korzyści.
- **Kafka** daje trwałość i możliwość podłączenia innych konsumentów w
  przyszłości (np. analityka tras, audyt, ewentualny multi-instance fan-out
  SSE przez konsumenta Kafki, gdyby delivery-service przestał być
  single-instance). Dziś `delivery.location` nie ma żadnego konsumenta — to
  świadomy wybór "gotowe pod przyszłość", nie zaległość do zamknięcia (patrz
  `md/todo-v2/03-kafka-mid-senior.md`, Część 1: ten topik został pominięty w
  zamykaniu osieroconych eventów właśnie z tego powodu).

Konsekwencja: `delivery.location` nie ma dead-letter topiku ani konsumenta w
tym serwisie — DLQ (Część 3.1) dotyczy tylko topików, które ten serwis
faktycznie konsumuje (`order.confirmed`, `order.prepared`).

## Brak `ErrorHandlingDeserializer` w restaurant-service

`RatingUpdatedListener` i `RestaurantIndexConsumer` w restaurant-service
przyjmują surowy `String` i same parsują JSON przez `ObjectMapper` wewnątrz
`try/catch`, logując błąd zamiast go propagować. Global
`spring.kafka.consumer.value-deserializer` w tym serwisie celowo zostaje
`StringDeserializer` — podmiana na `ErrorHandlingDeserializer` +
`JsonDeserializer` zepsułaby sygnaturę obu listenerów (parametr `String` nie
pasuje do zdeserializowanego obiektu) i nie dałaby żadnej korzyści, bo błędy
deserializacji są już przechwytywane wewnątrz metody, więc nigdy nie
dotarłyby do `DefaultErrorHandler`/DLQ na poziomie kontenera. Z tego samego
powodu restaurant-service nie ma `KafkaErrorHandlingConfig` ani DLT-topików —
byłyby martwym kodem. Ręczny commit offsetu (Część 3.3) został dodany mimo to,
bo jest bezpieczny niezależnie od strategii deserializacji.

## Znany dług: brak alertowania dla `payment.*.DLT`

Nowe DLT-topiki `payment.completed.DLT` i `payment.failed.DLT` (order-service,
Część 3.1) mają licznik Micrometer (`kafka.consumer.dlt.count`, tag `topic`),
ale nikt go dziś nie ogląda — repo nie ma reguł alertowych Prometheusa
(`monitoring/` zawiera tylko `prometheus.yml` scrape config i dashboardy
Grafany, brak plików `*.rules.yml`). Zdarzenie płatnościowe, które wyląduje
na DLT, dziś przejdzie bez śladu poza logiem i metryką, którą trzeba ręcznie
sprawdzić w Grafanie.

Świadomie NIE budujemy tu nowej infrastruktury alertowej — poza zakresem
`md/todo-v2/03-kafka-mid-senior.md` i wymagałoby to najpierw Alertmanagera,
którego w stacku nie ma. Zostaje to jako udokumentowany follow-up: reguła
alertu na `increase(kafka_consumer_dlt_count{topic=~"payment.*"}[5m]) > 0`,
gdy tylko Alertmanager pojawi się w stacku.
