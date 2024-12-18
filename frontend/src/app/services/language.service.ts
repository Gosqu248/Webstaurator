import { Injectable } from '@angular/core';
import { Translations } from "../interfaces/language.interface";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private currentLanguage: 'pl' | 'en' = 'pl';
  public languageChangeSubject = new BehaviorSubject<'pl' | 'en'>('pl');
  languageChange$ = this.languageChangeSubject.asObservable();



  constructor() {
    if(this.isLocalStorageAvailable()) {
      const savedLanguage = localStorage.getItem('language');
      if(savedLanguage === 'pl' || savedLanguage === 'en') {
        this.currentLanguage = savedLanguage;
        this.languageChangeSubject.next(this.currentLanguage);
      }
    }
  }

  translations: Translations = {
    pl: {
      chooseLanguage: 'Wybierz język',
      polish: 'Polski',
      english: 'Angielski',
      home1: 'Nie wiesz co dziś zjeść?',
      home2: 'Znajdź restauracje w pobliżu dla siebie',
      address: 'Podaj adres...',
      search: 'Szukaj',
      second1: 'Zamawianie jest proste.',
      second2: 'Wystarczą 3 kroki!',
      step1_1: 'Podaj swoją lokalizację',
      step1_2: 'Znajdziemy wszystkie restauracje w Twojej okolicy.',
      step2_1: 'Znajdź to, czego szukasz',
      step2_2: 'Szukaj interesującego Cię jedzenia w różnych restauracjach.',
      step3_1: 'Złóż zamówienie',
      step3_2: 'Wybierz dostawę lub osobisty odbiór.',
      third1: 'Dlaczego warto zamawiać na',
      third2: 'Webstraurator?',
      why1_1: 'Szybkość i wygoda',
      why1_2: 'Zamów jedzenie poprzez kilka kliknięć bez wychodzenia z domu.',
      why2_1: 'Bogata oferta',
      why2_2: 'Ponad 10 000 restauracji do wyboru w całym kraju.',
      why3_1: 'Gwarancja jakości',
      why3_2: 'Współpracujemy tylko z zaufanymi restauracjami, które oferują najwyższą jakość.',
      why4_1: 'Bezpieczeństwo',
      why4_2: 'Płatności online są szyfrowane, a dane są chronione.',
      myAccount: 'Moje konto',
      dontHaveAccount: 'Nie masz konta?',
      createNow: 'Załóż je teraz',
      createAccount: 'Załóż konto',
      login: 'Zaloguj się',
      profile: 'Twoje dane',
      orders: 'Historia zamówień',
      favourites: 'Ulubione',
      opinions: 'Opinie',
      help: 'Pomoc i Regulaminy',
      changeLanguage: 'Zmień język',
      language: 'Język',
      name: 'Nazwa użytkownika',
      email: 'E-mail',
      password: 'Hasło',
      confirmPassword: 'Potwierdź hasło',
      registerAccept: "Klikając przycisk 'Załóż konto' lub 'Zaloguj się przez Google', akceptujesz regulamin serwisu i polityką prywatności.",
      haveAccount: 'Masz już konto?',
      emailError: 'Nieprawidłowy adres email',
      passwordError: 'Hasło musi zawierać co najmniej 8 znaków, jedną wielką literę i jeden znak specjalny',
      confirmPasswordError: 'Hasła muszą być takie same',
      loginError: 'Nieprawidłowy login lub hasło',
      logout: 'Wyloguj',
      newName: "Zaktualizuj nazwisko",
      wannaChangePassword: 'Chcesz zmienić hasło?',
      clickHere: 'Kliknij tutaj',
      changePassword: "Zmień hasło",
      oldPassword: "Stare hasło",
      newPassword: "Nowe hasło",
      changePasswordError: "Błąd zmiany hasła",
      addresses: "Adresy",
      addAddress: "Dodaj adres",
      street: "Ulica",
      houseNumber: "Numer mieszkania",
      floorNumber: "Piętro",
      accessCode: "Kod dostępu",
      city: "Miasto",
      zipCode: "Kod pocztowy",
      phoneNumber: "Numer telefonu",
      changeAddress: "Edytuj adres",
      delete: "Usuń",
      updateAddress: "Zaktualizuj adres",
      delivery: "Dostawa",
      pickup: "Odbiór",
      sortBy: "Sortuj według",
      closeToMe: "Blisko mnie",
      grades: "Oceny",
      cheapestDelivery: "Opłata za dostawę",
      seeMap: "Zobacz na mapie",
      order: "Zamawiaj z ",
      restaurants: "restauracji",
      searchRestaurant: "Wyszukaj restaurację po nazwie...",
      deliveryClosed: "Dostawa wstrzymana",
      yourOrder: "Twoje zamówienie",
      basketEmpty: "Koszyk jest pusty",
      didntAdd: "Nie dodałeś jeszcze żadnych produktów.",
      notAvailable: "Niedostępne",
      searchMenu: "Wyszukaj w menu...",
      together: "Razem",
      deliveryCost: "Koszt dostawy",
      total: "Kwota całkowita",
      orderNow: "Zamów",
      edit: "Edytuj",
      aboutThisBusiness: "O firmie",
      reviews: "Opinie",
      info: "Informacje",
      overallScore: "Średnia ocena",
      outOf: "na podstawie ",
      reviews2: "opinii",
      allReviews: "Wszystkie opinie pochodzą od klientów Webstaurator, którzy zamówili jedzenie z ",
      quality: "Jakość",
      deliveryTime: "Czas dostawy",
      deliveryHour: "Godzina dostawy",
      monday: "Poniedziałek",
      tuesday: "Wtorek",
      wednesday: "Środa",
      thursday: "Czwartek",
      friday: "Piątek",
      saturday: "Sobota",
      sunday: "Niedziela",
      fees: "Opłaty",
      deliveryFee: "Opłata za dostawę",
      feesComment: "Minimalna kwota zamówienia",
      paymentMethods: "Metody płatności",
      restaurantInfo: "Informacje o restauracji",
      payU: "PayU",
      creditCard: "Karta kredytowa",
      cash: "Gotówka",
      minimumOrder: "Do minimalnej wartości zamówienia brakuje: ",
      chooseIngredients: "Wybierz składniki",
      addToBasket: "Dodaj do koszyka ",
      personalData: "Dane osobowe",
      additionalInformation: "Dodatkowe informacje",
      deliveryAddress: "Dostępne adresy dostawy",
      relevantInformation: "Wprowadź istotne informacje",
      planDelivery: "Zaplanuj dostawę",
      lackOfAddress: "Brak adresów dostawy",
      asSoonAsPossible: "Jak najszybciej",
      plan: "Zaplanuj",
      chooseHour: "Wybierz godzinę",
      selectHour: "Wybierz godzinę dostawy",
      confirm: "Potwierdź",
      cancel: "Anuluj",
      payment: "Sposób płatności",
      ordering: "Zamawiam",
      serviceFee: "Opłata serwisowa",
      planPickUp: "Zaplanuj obiór",
      selectHourPickUp: "Wybierz godzinę odbioru",
      mustLogin: "Musisz się zalogować",
      youAreLogin: "Jesteś zalogowany",
      paymentStatus: "Status płatności",
      paymentNumber: "Numer płatności",
      status: "Status",
      COMPLETED: " Opłacona",
      CANCELED: " Anulowana",
      paymentTitleSuccess: "Płatność zrealizowana pomyślnie. Zamówienie zostało przekazane do realizacji.",
      paymentTitleFailure: "Płatność nie powiodła się. Spróbuj ponownie.",
      goToOrders: "Przejdź do zamówień",
      backToCheckout: "Powrót do zamówienia",
      backToMainPage: "Powrót do strony głównej",
      orderFrom: "Zamówienie z ",
      orderNumber: "Numer zamówienia: ",
      quantity: "Ilość",
      showMenu: "Pokaż menu",
      hideMenu: "Ukryj menu",
      price: "Cena",
      deliveryMethod: "Sposób dostarczenia",
      addOpinion: "Dodaj opinię",
      addingOpinionFor: "Dodawanie opinii dla ",
      addComment: "Dodaj komentarz",
      locked: " Konto zablokowane. Spróbuj ponownie później.",
      sendCode: "Wyślij kod weryfikacyjny",
      twoFactorCode: "Kod weryfikacyjny",
      choose: "Wybierz",
      error2FA: "Nieprawidłowy kod werfikacyjny",
      yourLocation: "Twoja lokalizacja",
      closed: "Zamknięte",
      loginWithGoogle: "Zaloguj się przez Google",
      or: "LUB",
      phoneNumberError: "Nieprawidłowy numer telefonu",
      orderMonitoring: "Monitorowanie zamówień",
      userData: "Dane użytkownika",
      toAddress: "Na adres",
      userEmail: "Adres e-mail",
      notPaid: "Nieopłacone",
      paid: "Opłacone",
      paymentId: "ID płatności",
      changeStatus: "Zmień status",
      confirmChangeStatus: "Potwierdź zmianę statusu",
      confirmContent: "Czy na pewno chcesz zmienić status?",
      addRestaurant: "Dodaj restaurację",
      restaurantData: "Dane restauracji",
      restaurantName: "Nazwa ",
      restaurantCategory: "Kategoria ",
      restaurantImage: "Link do zdjęcia",
      restaurantLogo: "Link do logo",
      restaurantAddress: "Adres restauracji",
      flatNumber: "Numer lokalu",
      restaurantDeliveryData: "Dane dotyczące dostawy",
      deliveryMinTime: "Minimalny czas dostawy",
      deliveryMaxTime: "Maksymalny czas dostawy",
      deliveryPrice: "Opłata za dostawę",
      minimumPrice: "Minimalna cena zamówienia",
      pickupTime: "Czas odbioru",
      restaurantDeliveryHours: "Godziny otwarcia restauracji",
      openTime: "Godzina otwarcia",
      closeTime: "Godzina zamknięcia",
      restaurantMenu: "Menu restauracji",
      addMenu: "Dodaj menu",
      category: "Kategoria",
      image: "Link do zdjęcia",
      ingredients: "Składniki",
      addAdditives: "Dodaj dodatek",
      additive: "Dodatek",
      type: "Typ",
      removeMenuItem: "Usuń pozycję z menu",
      addMenuItem: "Dodaj pozycję do menu",
      removeAdditive: "Usuń dodatek",
      availableRestaurants: "Dostępne restauracje"

    },
    en: {
      chooseLanguage: 'Choose language',
      polish: 'Polish',
      english: 'English',
      home1: 'Don\'t know what to eat today?',
      home2: 'Find restaurants nearby for yourself',
      address: 'Enter address...',
      search: 'Search',
      second1: 'Ordering is easy.',
      second2: 'Just 3 steps!',
      step1_1: 'Enter your location',
      step1_2: 'We will find all the restaurants in your area',
      step2_1: 'Find what you are looking for',
      step2_2: 'Search for the food you are interested in in different restaurants.',
      step3_1: 'Place an order',
      step3_2: 'Choose delivery or personal pickup.',
      third1: 'Why is it worth ordering at',
      third2: 'Webstraurator?',
      why1_1: 'Speed and convenience',
      why1_2: 'Order food in a few clicks without leaving your home.',
      why2_1: 'Rich offer',
      why2_2: 'Over 10,000 restaurants to choose from nationwide.',
      why3_1: 'Quality guarantee',
      why3_2: 'We only cooperate with trusted restaurants that offer the highest quality.',
      why4_1: 'Security',
      why4_2: 'Online payments are encrypted and data is protected.',
      myAccount: 'My account',
      dontHaveAccount: 'Don\'t have an account?',
      createNow: 'Create it now',
      createAccount: 'Create account',
      login: 'Log in',
      profile: 'Your data',
      orders: 'Order history',
      favourites: 'Favourites',
      opinions: 'Opinions',
      help: 'Help and Terms',
      changeLanguage: 'Change language',
      language: 'Language',
      name: 'Name',
      email: 'Email address',
      password: 'Password',
      confirmPassword: 'Confirm password',
      registerAccept: "By clicking the 'Create account' button or 'Login with Google', you accept the terms of service and privacy policy.",
      haveAccount: 'Already have an account?',
      emailError: 'Invalid email address',
      passwordError: 'The password must be at least 8 characters long, contain one uppercase letter and one special character',
      confirmPasswordError: 'Passwords must match',
      loginError: 'Invalid login or password',
      logout: 'Logout',
      newName: "Update name",
      wannaChangePassword: 'Do you want to change your password?',
      clickHere: 'Click here',
      changePassword: "Change password",
      oldPassword: "Old password",
      newPassword: "New password",
      changePasswordError: "Error changing password",
      addresses: "Addresses",
      addAddress: "Add address",
      street: "Street",
      houseNumber: "House number",
      floorNumber: "Floor",
      accessCode: "Access code",
      city: "City",
      zipCode: "Zip code",
      phoneNumber: "Phone number",
      changeAddress: "Edit address",
      delete: "Delete",
      updateAddress: "Update address",
      delivery: "Delivery",
      pickup: "Pickup",
      sortBy: "Sort by",
      closeToMe: "Close to me",
      grades: "Grades",
      cheapestDelivery: "Delivery fee",
      seeMap: "See on map",
      order: "Order from ",
      restaurants: "restaurants",
      searchRestaurant: "Search for a restaurant by name...",
      deliveryClosed: "Closed to delivery",
      yourOrder: "Your order",
      basketEmpty: "Basket is empty",
      didntAdd: "You haven't added any products yet.",
      notAvailable: "Not available",
      searchMenu: "Search in menu...",
      together: "Subtotal",
      deliveryCost: "Delivery cost",
      total: "Total",
      orderNow: "Order",
      edit: "Edit",
      aboutThisBusiness: "About this business",
      reviews: "Reviews",
      info: "Information",
      overallScore: "Overall score",
      outOf: "out of ",
      reviews2: "reviews",
      allReviews: "All reviews come from Webstaurator customers who ordered food from ",
      quality: "Quality",
      deliveryTime: "Delivery time",
      deliveryHour: "Delivery hour",
      monday: "Monday",
      tuesday: "Tuesday",
      wednesday: "Wednesday",
      thursday: "Thursday",
      friday: "Friday",
      saturday: "Saturday",
      sunday: "Sunday",
      fees: "Fees",
      deliveryFee: "Delivery fee",
      feesComment: "Minimum order amount",
      paymentMethods: "Payment methods",
      restaurantInfo: "Restaurant information",
      payU: "PayU",
      creditCard: "Credit Card",
      cash: "Cash",
      minimumOrder: "The minimum order value is missing: ",
      chooseIngredients: "Choose ingredients",
      addToBasket: "Add to basket ",
      personalData: "Personal data",
      additionalInformation: "Additional information",
      deliveryAddress: " Available delivery addresses",
      relevantInformation: "Enter relevant information",
      planDelivery: "Plan delivery",
      lackOfAddress: "No delivery addresses",
      asSoonAsPossible: "As soon as possible",
      plan: "Plan",
      chooseHour: "Choose hour",
      selectHour: "Select delivery hour",
      confirm: "Confirm",
      cancel: "Cancel",
      payment: "Payment method",
      ordering: "I'm ordering",
      serviceFee: "Service fee",
      planPickUp: "Plan pickup",
      selectHourPickUp: "Select pickup hour",
      mustLogin: "You must log in",
      youAreLogin: "You are logged in",
      paymentStatus: "Payment status",
      paymentNumber: "Payment number",
      status: "Status",
      COMPLETED: " Completed",
      CANCELED: " Canceled",
      paymentTitleSuccess: "Payment completed successfully. The order has been passed for processing.",
      paymentTitleFailure: "Payment failed. Try again.",
      goToOrders: "Go to orders",
      backToCheckout: "Back to checkout",
      backToMainPage: "Back to main page",
      orderFrom: "Order from ",
      orderNumber: "Order number: ",
      quantity: "Quantity",
      showMenu: "Show menu",
      hideMenu: "Hide menu",
      price: "Price",
      deliveryMethod: "Delivery method",
      addOpinion: "Add opinion",
      addingOpinionFor: "Adding opinion for ",
      addComment: "Add comment",
      locked: "Account is locked. Try again later.",
      sendCode: "Send verification code",
      twoFactorCode: "Verification code",
      choose: "Choose",
      error2FA: "Invalid verification code",
      yourLocation: "Your location",
      closed: "Closed",
      loginWithGoogle: "Login with Google",
      or: "OR",
      phoneNumberError: "Invalid phone number",
      orderMonitoring: "Order monitoring",
      userData: "User data",
      toAddress: "To address",
      userEmail: "Email",
      notPaid: "Not paid",
      paid: "Paid",
      paymentId: "Payment ID",
      changeStatus: "Change status",
      confirmChangeStatus: "Confirm Status Change",
      confirmContent: "Are you sure you want to change the status?",
      addRestaurant: "Add restaurant",
      restaurantData: "Restaurant data",
      restaurantName: "Name",
      restaurantCategory: "Category",
      restaurantImage: "Image",
      restaurantLogo: "Logo url",
      restaurantAddress: "Restaurant address",
      flatNumber: "Flat number",
      restaurantDeliveryData: "Delivery data",
      deliveryMinTime: "Minimum delivery time",
      deliveryMaxTime: "Maximum delivery time",
      deliveryPrice: "Delivery fee",
      minimumPrice: "Minimum order price",
      pickupTime: "Pickup time",
      restaurantDeliveryHours: "Restaurant opening hours",
      openTime: "Opening hour",
      closeTime: "Closing hour",
      restaurantMenu: "Restaurant menu",
      addMenu: "Add menu",
      category: "Category",
      image: "Image url",
      ingredients: "Ingredients",
      addAdditives: "Add additives",
      additive: "Additive",
      type: "Type",
      removeMenuItem: "Remove menu item",
      addMenuItem: "Add menu item",
      removeAdditive: "Remove additive",
      availableRestaurants: "Available restaurants"
    }
  };

  getCurrentLanguage() {
    return this.currentLanguage;
  }

  switchLanguage() {
    this.currentLanguage = this.currentLanguage === 'pl' ? 'en' : 'pl';
    localStorage.setItem('language', this.currentLanguage);
    this.languageChangeSubject.next(this.currentLanguage);
  }

  getTranslation<K extends keyof Translations[typeof this.currentLanguage]>(key: K) {
    return this.translations[this.currentLanguage][key];
  }

  isLocalStorageAvailable() {
    return typeof localStorage !== 'undefined';
  }
}
