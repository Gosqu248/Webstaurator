export interface Delivery {
  id: number;
  deliveryMinTime: number;
  deliveryMaxTime: number;
  deliveryPrice: number;
  minimumPrice: number;
  pickupTime: number;
}
export interface DeliveryHour {
  id: number;
  dayOfWeek: number;
  openTime: string;
  closeTime: string;
}
