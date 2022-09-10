import { Component, Inject, OnInit } from '@angular/core';
import { FileService } from './file.service';
import { MyFile } from './file';
import { DOCUMENT } from '@angular/common';
import { NgForm } from '@angular/forms';




@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
 
  title = 'SecuriFile';
  
}

