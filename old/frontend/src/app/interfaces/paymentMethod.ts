export interface PaymentMethod {
  method: string;
  image: string;
}

export interface PaymentResponse {
  redirectUri: string;
  orderId: number;
}
