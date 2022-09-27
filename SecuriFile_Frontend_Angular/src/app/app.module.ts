import { HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { FileService } from './file.service';
import { AdminComponent } from './admin/admin.component';
import { ClientenvComponent } from './clientenv/clientenv.component';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from './header/header.component';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { initializeKeycloak } from './utility/keycloak.init';
import { AuthGuard } from './utility/keycloak.guard';
import { LoginComponent } from './login/login.component';



@NgModule({
  declarations: [
    AppComponent,
    AdminComponent,
    ClientenvComponent,
    HeaderComponent,
    LoginComponent,
   
  
  ],
  imports: [
    BrowserModule,HttpClientModule,FormsModule,
    RouterModule.forRoot([
      {path:'',component:LoginComponent},
      {path: 'admin', component: AdminComponent,canActivate:[AuthGuard] ,data:{roles:['admin']}},
      {path: 'client', component: ClientenvComponent,canActivate:[AuthGuard] },
    ]),
    KeycloakAngularModule
  ],
  providers: [FileService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService]
    }],
  bootstrap: [AppComponent]
})
export class AppModule { }
