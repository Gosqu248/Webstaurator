import {UserDTO} from "./user.interface";

export interface Login {
  accessToken: string
  user: UserDTO
}
