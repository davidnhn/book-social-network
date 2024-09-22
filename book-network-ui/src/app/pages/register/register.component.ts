import { Component } from '@angular/core';
import {RegistrationRequest} from "../../services/models/registration-request";
import {Router} from "@angular/router";
import {AuthentificationService} from "../../services/services/authentification.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

  registerRequest: RegistrationRequest = {email: "", firstname: "", lastname: "", password: ""};
  errorMsg: Array<String> = [];

  constructor(
    private router: Router,
    private authService: AuthentificationService
  ) {
  }

  register() {
    this.errorMsg = [];
    this.authService.register({
      body: this.registerRequest
    }).subscribe({
      next: () => {
        this.router.navigate(["activate-account"]);
      },
      error: err => {
        this.errorMsg = err.error.validationErrors;
      }
    })
  }

  login() {
    this.router.navigate(["login"]);
  }
}
