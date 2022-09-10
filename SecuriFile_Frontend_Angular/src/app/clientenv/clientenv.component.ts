
import { Component, Inject, OnInit } from '@angular/core';

import { DOCUMENT } from '@angular/common';
import { NgForm } from '@angular/forms';
import { MyFile } from '../file';
import { FileService } from '../file.service';
import * as FileSaver from 'file-saver';

@Component({
  selector: 'app-clientenv',
  templateUrl: './clientenv.component.html',
  styleUrls: ['./clientenv.component.css']
})
export class ClientenvComponent implements OnInit {
    userName:string = "Haythem"
    title = 'Crypto';
    public fileInit: MyFile =  {
      id :'init',
      fileName:'init',
      dateOfCreation:'init',
      clientName:'init',
      encrypted:false
    }
   
    public files: MyFile[] =[]  ;
    public deleteFile: MyFile=this.fileInit;
    public addFile: File | undefined;
    radioValue: string="true";
      
    
    constructor(private fileService: FileService,@Inject(DOCUMENT) private document: Document){}
    
    public getFiles():void{
      this.fileService.getByClient(this.userName).subscribe(
        (response : MyFile[])=>{
            this.files=response;
        }
      );
      
    }
   
    ngOnInit() {
      this.getFiles();
      
  } 
  public deleteModel(file:MyFile):void{
    const container =this.document.getElementById('main-container');
    const button = document.createElement("button");
    button.type='button';
    button.style.display='none';
    button.setAttribute('data-toggle','modal');
    button.setAttribute('data-target','#deleteFileModel');
    this.deleteFile =file;
  
    container?.appendChild(button);
    button.click(); 
     
  }
  
  public onDelete(fileId:string):void{
    this.document.getElementById('noButton')?.click();
    this.fileService.deleteFile(fileId).subscribe(
      (response:void)=>{
        console.log(response);
        this.getFiles();
       
      }
    )
  }
  public createFile(event:any){
    this.addFile = event.target.files[0];
     
  }
  public onAddFile(addForm:NgForm):void{
    this.document.getElementById('cancelButton')?.click();
  
    if (this.addFile) {
  
     
  
      const formData = new FormData();
  
      formData.append("file", this.addFile);
      console.log(formData);
  
      this.fileService.addFileByUser(formData,this.userName, this.radioValue).subscribe(
        () => {
          this.getFiles();
        }
          );
       
      
  }
  } 
  
  public radioChangeHandler(event:any) :void{
    this.radioValue=event.target.value;
  }
  public onDownload(file:MyFile):void{
    
    this.fileService.downloadFile(file.id+file.fileName).subscribe(
      (response:void)=>{
        console.log(response);
        this.getFiles();
        alert(file.fileName + " Downloaded successfully")
       
      }
    )
  }
  public onFinalDownload(file:MyFile):void{
    extention   : file.fileName.substring(file.fileName.lastIndexOf('.')+1);
    
    this.fileService.finaldownloadFile(file.id,file.fileName).subscribe(
      (response)=>{
        let content = JSON.stringify(response)
        FileSaver.saveAs(new Blob([response]),file.fileName);
        this.fileService.clearDownloadFolder(file.id+file.fileName).subscribe();
      }
    )
    
  }


  
}
