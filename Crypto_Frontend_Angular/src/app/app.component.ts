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
  test:File={
    uuid:"dsqsdqsq",
    fileName:"test",
    dateOfCreation:"sdsd"
  }
  test2:File={
    uuid:"dsqsdqsq",
    fileName:"test2",
    dateOfCreation:"sdsd"
  }
  public files: File[] =[this.test,this.test2]  ;
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
