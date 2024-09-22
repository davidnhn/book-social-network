import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {TokenService} from "../../../../services/token/token.service";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit{

  constructor(
    private router :Router,
    private tokenService: TokenService
  ) {
  }

  ngOnInit() {
    const linkColor = document.querySelectorAll('.nav-link');
    linkColor.forEach(link => {
      if (window.location.href.endsWith(link.getAttribute('href') || '')) {
        link.classList.add("active"); // on ajout la classe au chargement de la page si c'est le lien du menu qui correspond
      }
      link.addEventListener('click', () => {
        linkColor.forEach(l => l.classList.remove('active'));
        link.classList.add("active") // au click on ajout la classe au lien cliqué , on l'enleve aux autre
      })
    })
  }

  logout() {
      localStorage.removeItem('token');
      window.location.reload();
  }
}
