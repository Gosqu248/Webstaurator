import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {LanguageService} from "../../../services/language.service";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {UserInfoOrder} from "../../../interfaces/user-info-order";

@Component({
  selector: 'app-order-personal-info',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './order-personal-info.component.html',
  styleUrl: './order-personal-info.component.css'
})
export class OrderPersonalInfoComponent implements OnChanges{
  @Input() userInfo!: UserInfoOrder | null;
  personalForm: FormGroup;
  relevantInformation: string = '';

  constructor(private languageService: LanguageService,
              private fb: FormBuilder) {
    this.personalForm = this.fb.group({
      name: [''],
      email: [''],
      phoneNumber: [''],
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if(changes['userInfo'] && this.userInfo) {
      this.personalForm.patchValue({
        name: this.userInfo.name,
        email: this.userInfo.email,
        phoneNumber: this.userInfo.phoneNumber
      });
    } else {
      this.personalForm.reset();
    }
  }

  getTranslation<k extends keyof LanguageTranslations>(key: k): string {
    return this.languageService.getTranslation(key);
  }
}
