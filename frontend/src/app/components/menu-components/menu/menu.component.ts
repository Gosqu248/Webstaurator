import {Component, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";
import {LanguageService} from "../../../services/language.service";
import {AuthService} from "../../../services/auth.service";
import {LanguageTranslations} from "../../../interfaces/language.interface";
import {MenuLanguageComponent} from "../menu-language/menu-language.component";
import {MenuRegisterComponent} from "../menu-register/menu-register.component";
import {MenuLoginComponent} from "../menu-login/menu-login.component";
import {MenuProfileComponent} from "../menu-profile/menu-profile.component";
import {Router, RouterLink, RouterOutlet} from "@angular/router";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MenuAddressesComponent} from "../menu-addresses/menu-addresses.component";
import {MenuFavouriteComponent} from "../menu-favourite/menu-favourite.component";

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    NgIf,
    MenuLanguageComponent,
    MenuRegisterComponent,
    MenuLoginComponent,
    MenuProfileComponent,
    RouterLink,
    RouterOutlet
  ],
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  showLanguageOption: boolean = false;
  role: string = '';
  name: string = '';
  private isFetchingUserData: boolean = false;
  constructor(private languageService: LanguageService,
              protected authService: AuthService,
              private dialog: MatDialog,
              private router: Router,
              public dialogRef: MatDialogRef<MenuComponent>) {}

  ngOnInit() {
    this.getUserData();
    this.checkWindowWidth();
  }

  getTranslation<K extends keyof LanguageTranslations>(key: K) {
    return this.languageService.getTranslation(key);
  }

  getUserData() {
    if (this.isFetchingUserData) return;
    this.isFetchingUserData = true;
    const token = localStorage.getItem('jwt');
    console.log('Token', token);
    if (token) {
      this.authService.getUser(token).subscribe({
        next: user => {
          this.name = user.name;
          localStorage.setItem('name', user.name);
          localStorage.setItem('email', user.email);
          localStorage.setItem('role', user.role);
          this.role = user.role;
          if (user.id) {
            localStorage.setItem('userId', user.id.toString())
          }

          this.isFetchingUserData = false;
        },
        error: error => {
          console.error('Error fetching user data', error);
          this.isFetchingUserData = false;
        }
      });
    } else {
      console.log('No token found');
      this.isFetchingUserData = false;
    }
  }

  checkWindowWidth() {
    this.showLanguageOption = window.innerWidth < 768;
  }



  logout() {
    this.authService.logout();
  }

  closeDialog() {
    this.dialogRef.close();
  }

  openRegisterDialog() {
    this.closeDialog();
    this.dialog.open(MenuRegisterComponent, {
      width: 'auto',
      maxWidth: '100%',
      height: 'auto',
    });
  }

  openLoginDialog() {
    this.closeDialog();
    this.dialog.open(MenuLoginComponent, {
      width: 'auto',
      maxWidth: '100%',
      height: 'auto',
    });
  }

  openMenuProfileDialog() {
    this.closeDialog();
    if (this.authService.isAuthenticated()) {
      this.dialog.open(MenuProfileComponent, {
        width: 'auto',
        maxWidth: '100%',
        height: 'auto',
      });
    } else {
      this.dialog.open(MenuLoginComponent, {
        width: 'auto',
        maxWidth: '100%',
        height: 'auto',
      });
    }
  }

  openAddressesDialog() {
    this.closeDialog();
    if (this.authService.isAuthenticated()) {
      this.dialog.open(MenuAddressesComponent, {
        width: 'auto',
        maxWidth: '100%',
        height: 'auto',
      });
    } else {
      this.dialog.open(MenuLoginComponent, {
        width: 'auto',
        maxWidth: '100%',
        height: 'auto',
      });
    }
  }

  openFavoritesDialog() {
    this.closeDialog();
    this.dialog.open(MenuFavouriteComponent, {
      width: 'auto',
      maxWidth: '100%',
      height: 'auto',
    });
  }

  goToOrderHistory() {
    this.dialog.closeAll();
    this.router.navigate(['/orders-history']);

  }

  openLanguageDialog() {
    this.closeDialog();
    this.dialog.open(MenuLanguageComponent, {
      width: 'auto',
      maxWidth: '100%',
      height: 'auto',
    });
  }


  goToOrderMonitor() {
    this.dialog.closeAll();
    this.router.navigate(['/order-monitoring']);
  }

  goToAddRestaurant() {
    this.dialog.closeAll();
    this.router.navigate(['/add-restaurant']);
  }


  goToRestaurants() {
    this.dialog.closeAll();
    this.router.navigate(['/all-restaurants']);
  }
}
