import { DOCUMENT } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Client } from '../client';
import { ClientService } from '../client.service';
import { MyFile } from '../file';
import { FileService } from '../file.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  public fileInit: MyFile =  {
    id :'init',
    fileName:'init',
    dateOfCreation:'init',
    clientName:'init',
    encrypted:false
  }
  public clientinit:Client={
    clientName:"init",
   
  }
  public deleteClient:Client = this.clientinit;
  public deleteFile: MyFile=this.fileInit;
    public addFile: File | undefined;
    radioValue: string="true";
  files: MyFile[]=[];
  prvKey: string="";
  sKey: string="";
  clientname: string="";
  addClient: Client=this.clientinit;
   


  constructor(private clientService: ClientService,private fileService: FileService,@Inject(DOCUMENT) private document: Document) { }
  clients : Client[]=[];
  ngOnInit(): void {
      this.clientService.getClients().subscribe(
        (response:Client[])=>{
            this.clients=response;
        }
      );
  }
 public getClients():void{
    this.clientService.getClients().subscribe(
      (response:Client[])=>{
          this.clients=response;
      }
    );
  }
public saveCoord(client:Client){
  this.addClient=client;
}
    public createFile(event:any){
    this.addFile = event.target.files[0];
     
  }
  public onAddFileClient(addForm:NgForm,clientName:string):void{
    this.document.getElementById('cancelSendButton')?.click();
  
    if (this.addFile) {
  
     
  
      const formData = new FormData();
  
      formData.append("file", this.addFile);
      console.log(this.clientname);
      
  
      this.fileService.addFileByUser(formData,clientName, this.radioValue).subscribe(
        () => {
          this.getFiles();
        }
          );
       
      
  }
  } 
  public getFiles():void{
    this.fileService.getFiles().subscribe(
      (response : MyFile[])=>{
          this.files=response;
      }
    );
    
  }
  
  public radioChangeHandler(event:any) :void{
    this.radioValue=event.target.value;
  }
  public onDownload(file:MyFile):void{
    
    this.fileService.downloadFile(file.id+file.fileName).subscribe(
      (response:any)=>{
        console.log(response);
        this.getFiles();
        alert(file.fileName + " Downloaded successfully")
       
      }
    )
  }
  public deleteModel(client:Client):void{
    
    this.deleteClient =client;
  
    
  }
  public onDeleteClient(clientName:string):void{
    this.document.getElementById("noClientButton")?.click();
    console.log("clientName" + clientName);
      this.clientService.deleteClient(clientName).subscribe(
        (Response: void)=>{
          this.getClients();
        }
      );
      
  }
  public onAddClient(form:NgForm):void{
    this.document.getElementById("clientCancelButton")?.click();
    this.clientService.addClient(this.clientname).subscribe(
      (Response: void)=>{
        this.getClients();
      }
    );
    
}
  


  
}


