export interface UserAddress {
  id?: number;
  street: string;
  houseNumber: string;
  city: string;
  zipCode: string;
  phoneNumber: string;
  floorNumber?: string;
  accessCode?: string;
}
