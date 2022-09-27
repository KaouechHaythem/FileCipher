
import { Component, Inject, OnInit } from '@angular/core';

import { DOCUMENT } from '@angular/common';
import { NgForm } from '@angular/forms';
import { MyFile } from '../file';
import { FileService } from '../file.service';
import * as FileSaver from 'file-saver';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-clientenv',
  templateUrl: './clientenv.component.html',
  styleUrls: ['./clientenv.component.css']
})
export class ClientenvComponent implements OnInit {
 
    userName: string='';
    title = 'Crypto';
    public fileInit: MyFile =  {
      id :'init',
      fileName:'init',
      dateOfCreation:'init',
      clientName:'init',
      encrypted:false
    }
   //store all the files belonging to current user
    public files: MyFile[] =[]  ;
   //store the file to be deleted when calling function deleteModel
    public deleteFile: MyFile=this.fileInit;
   // store the file to add when calling function createFile
    public addFile: File | undefined;
   // store the value of the radio (encrypted or nor when adding a file)
    radioValue: string="true";
      
    
    constructor(private fileService: FileService,@Inject(DOCUMENT) private document: Document,private keyCloakService:KeycloakService){}
    // return all the files belonging to current user
    public getFiles():void{
      this.fileService.getByClient(this.userName).subscribe(
        (response : MyFile[])=>{
            this.files=response;
        }
      );
      
    }
   // call getFiles all files  when  this webpage is loaded
    ngOnInit() {
      
      this.userName=this.keyCloakService.getUsername();
      this.getFiles();
      console.log(this.userName);
      
  } 
  // show the delete model when delete button is clicked
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
  // this function deletes a file
  public onDelete(fileId:string):void{
    this.document.getElementById('noButton')?.click();
    this.fileService.deleteFile(fileId).subscribe(
      (response:void)=>{
        console.log(response);
        this.getFiles();
       
      }
    )
  }
  // when the file is chosen in the input form it will be stored in addFile
  public createFile(event:any){
    this.addFile = event.target.files[0];
     
  }
  //add a file to the current user
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
  //detecting when the value of the radio (encrypted or not) changes and stores its value
  public radioChangeHandler(event:any) :void{
    this.radioValue=event.target.value;
  }
  /*this was meant to download the file locally to the server (not useful anymore)
  public onDownload(file:MyFile):void{
    
    this.fileService.downloadFile(file.id+file.fileName).subscribe(
      (response:void)=>{
        console.log(response);
        this.getFiles();
        alert(file.fileName + " Downloaded successfully")
       
      }
    )
  }*/
  // download current file 
  public onFinalDownload(file:MyFile):void{
    
    
    this.fileService.finaldownloadFile(file.id,file.fileName).subscribe(
      (response)=>{
        let content = JSON.stringify(response)
        FileSaver.saveAs(new Blob([response]),file.fileName);
        this.fileService.clearDownloadFolder(file.id+file.fileName).subscribe();
      }
    )
    
  }


  
}
