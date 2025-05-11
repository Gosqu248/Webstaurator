import { Directive, HostListener } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
  standalone: true,
  selector: '[appPhoneNumberFormatter]'
})
export class PhoneNumberFormatterDirective {
  constructor(private ngControl: NgControl) {}

  @HostListener('input', ['$event.target.value'])
  onInput(value: string): void {
    const formattedValue = this.formatPhoneNumber(value);
    this.ngControl.control?.setValue(formattedValue, { emitEvent: false });
  }

  private formatPhoneNumber(value: string): string {
    // Usuwa wszystkie znaki niebędące cyframi
    const digits = value.replace(/\D/g, '');
    // Formatuje numer w stylu 123 456 789
    return digits.replace(/(\d{3})(\d{3})(\d{0,3})/, '$1 $2 $3').trim();
  }
}
