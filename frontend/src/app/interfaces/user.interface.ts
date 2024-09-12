import {UserAddress} from "./user.address.interface";

export interface User {
  name: string;
  email: string;
  password: string;
  role: string;
  addresses?: UserAddress[];

}
