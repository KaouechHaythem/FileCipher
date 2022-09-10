import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { FileService } from './file.service';
import { AdminComponent } from './admin/admin.component';
import { ClientenvComponent } from './clientenv/clientenv.component';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from './header/header.component';


@NgModule({
  declarations: [
    AppComponent,
    AdminComponent,
    ClientenvComponent,
    HeaderComponent,
  
  ],
  imports: [
    BrowserModule,HttpClientModule,FormsModule,
    RouterModule.forRoot([
      {path: 'admin', component: AdminComponent},
      {path: 'client/Haythem', component: ClientenvComponent},
    ])
  ],
  providers: [FileService],
  bootstrap: [AppComponent]
})
export class AppModule { }
