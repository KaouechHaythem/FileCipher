import { Component, OnInit } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
 
  constructor(private keyCloakService:KeycloakService ) { }

  ngOnInit(): void {
  }
  logout():void{
      this.keyCloakService?.logout('http://localhost:4200/');
      
  }

}
