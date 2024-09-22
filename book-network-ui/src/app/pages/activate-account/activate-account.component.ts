import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {AuthentificationService} from "../../services/services/authentification.service";

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.scss']
})
export class ActivateAccountComponent {

  message: string = '';
  isOkay: boolean = true;
  submitted: boolean = false;

  constructor(
    private router: Router,
    private authService: AuthentificationService
  ) {
  }

  onCodeCompleted(token: string) {
    this.confirmAccount(token);
  }

  confirmAccount(token: string) {
      this.authService.confirm({
        token
      }).subscribe({
        next: () => {
          this.message = 'Your account has been activated.\nNow you can proceed to login';
          this.submitted = true;
          this.isOkay = true;
        },
        error: () => {
          this.message = 'Your token has expired or is invalid';
          this.submitted = true;
          this.isOkay = false;
        }
      })
    }

  redirectToLogin() {
    this.router.navigate(["login"])
  }
}