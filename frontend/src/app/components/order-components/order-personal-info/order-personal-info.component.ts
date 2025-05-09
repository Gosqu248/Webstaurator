import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/state/language.service";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {UserDTO} from '../../../interfaces/user.interface';
import {AddressesService} from "../../../services/api/addresses.service";

@Component({
  selector: 'app-order-personal-info',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './order-personal-info.component.html',
  styleUrl: './order-personal-info.component.css'
})
export class OrderPersonalInfoComponent implements OnInit, OnChanges{
  @Output() personalInfoChanged = new EventEmitter<void>();
  @Input() user!: UserDTO;
  personalForm: FormGroup;
  relevantInformation: string = '';

  constructor(private languageService: LanguageService,
              private addressService: AddressesService,
              private fb: FormBuilder) {
    this.personalForm = this.fb.group({
      name: [''],
      email: [''],
      phoneNumber: [''],
    });

    this.personalForm.valueChanges.subscribe(() => {
      this.personalInfoChanged.emit();
    });
  }

  ngOnInit() {
    this.addressService.phoneNumber$.subscribe(phoneNumber => {
      this.personalForm.patchValue({
        phoneNumber: phoneNumber
      });
    });
  }


  ngOnChanges(changes: SimpleChanges) {
    if(changes['user'] && this.user) {

      this.personalForm.patchValue({
        name: this.user.name,
        email: this.user.email,
      });
    } else {
      this.personalForm.reset();
    }
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
