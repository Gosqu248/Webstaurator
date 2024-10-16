import { Additives } from '../interfaces/menu';

export class CalculateAdditiveUtil {
  static calculateAdditivePrice(additives: Additives[]): number {
    return additives.reduce((total, additive) => total + (additive.price ?? 0), 0);
  }
}
