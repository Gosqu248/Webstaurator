import { Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn, Validators
} from "@angular/forms";
import {LanguageService} from "../../../services/state/language.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {NgClass, NgIf} from "@angular/common";
import {Router} from "@angular/router";
import {AuthService} from "../../../services/api/auth.service";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MenuProfileComponent} from "../menu-profile/menu-profile.component";

@Component({
  selector: 'app-menu-change-password',
  standalone: true,
  imports: [
    FormsModule,
    NgClass,
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './menu-change-password.component.html',
  styleUrl: './menu-change-password.component.css'
})
export class MenuChangePasswordComponent {
  isVisibleOldPassword: boolean = false;
  isVisibleNewPassword: boolean = false;
  isVisibleConfirm: boolean = false;
  resetForm: FormGroup;
  isError: boolean = false;

  constructor(private languageService: LanguageService,
              private fb: FormBuilder,
              private authService: AuthService,
              private router: Router,
              private dialog: MatDialog,
              public dialogRef: MatDialogRef<MenuChangePasswordComponent>,
              ) {
    this.resetForm = this.fb.group({
      oldPassword: [''],
      newPassword: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[A-Z])(?=.*[!#]).*$/)
      ]],
      confirmPassword: ['', Validators.required],
    }, {validators: this.passwordMatchValidator});
  }

  changePassword() {
    if (this.resetForm.valid) {

      const token = localStorage.getItem('jwt');

      const oldPassword = this.resetForm.value.oldPassword
      const newPassword = this.resetForm.value.newPassword

      if(token) {
        this.authService.changePassword(token, oldPassword, newPassword).subscribe({
          next: response => {
            console.log('Password changed: ', response);
            this.backToMenuProfileDialog();
          },
          error: () => {
            console.log('Error changing password');
            this.isError = true;
          }
        });
      }

    }
  }


  getTranslation<K extends keyof LanguageTranslations>(key: K): string {
    return this.languageService.getTranslation(key);
  }

  passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    const newPassword = control.get('newPassword');
    const confirmPassword = control.get('confirmPassword');

    return newPassword && confirmPassword && newPassword.value !== confirmPassword.value
      ? { passwordMismatch: true }
      : null;
  };

  showErrorFor(controlName: string): boolean {
    const control = this.resetForm.get(controlName);
    if (controlName === 'confirmPassword') {
      return control ? control.invalid && (control.dirty || control.touched) && control.value !== '' || this.resetForm.hasError('passwordMismatch') && control.value : false;
    }
    return control ? control.invalid && (control.dirty || control.touched) && control.value !== '' : false;
  }

  togglePasswordVisibility(fieldId: string) {
    const inputField = document.getElementById(fieldId) as HTMLInputElement;
    inputField.type === 'password' ? inputField.type = 'text' : inputField.type = 'password';

    fieldId === 'oldPassword' ? this.isVisibleOldPassword = !this.isVisibleOldPassword : fieldId === 'newPassword' ? this.isVisibleNewPassword = !this.isVisibleNewPassword : this.isVisibleConfirm = !this.isVisibleConfirm;

  }
  closeDialog() {
    this.dialogRef.close();
  }

  backToMenuProfileDialog() {
    this.closeDialog();
    this.dialog.open(MenuProfileComponent);
  }

}
