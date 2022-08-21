import { Component, OnInit } from '@angular/core';
import { FileService } from './file.service';
import { File } from './file';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent /*implements OnInit*/{
  title = 'Crypto';
 
  public files: File[] =[]  ;
  constructor(private fileService: FileService){}
  
  public getFiles():void{
    this.fileService.getFiles().subscribe(
      (response : File[])=>{
          this.files=response;
      }
    );
    
  }
  /*why not working
  ngOnInit() {
    window.console.log('FROM ngOnInit()');
}*/ 

}
