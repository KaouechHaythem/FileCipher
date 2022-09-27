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
//inizialize a Client instance
  public clientinit:Client={
    clientName:"init",
   
  }
 // store the client to be deleted 
  public deleteClient:Client = this.clientinit;
 // store the file to be sent
    public addFile: File | undefined;
 // store the value of the radio (encrypted or not ) during file sending   
    radioValue: string="true";
  
 // store the name of the client add
  clientname: string="";
  // store the password of the client add
  password: string="";
 // store the value of the the client to be added
  addClient: Client=this.clientinit;
  //store the list of all the clients
  clients : Client[]=[];


  constructor(private clientService: ClientService,private fileService: FileService,@Inject(DOCUMENT) private document: Document) { }
  //show all the clients when this page loads
  ngOnInit(): void {
      this.clientService.getClients().subscribe(
        (response:Client[])=>{
            this.clients=response;
        }
      );
  }
  // store all the clients in table clients
 public getClients():void{
    this.clientService.getClients().subscribe(
      (response:Client[])=>{
          this.clients=response;
      }
    );
  }
  //save the client to be added
public saveCoord(client:Client){
  this.addClient=client;
}
// when the file is chosen in the input form it will be stored in addFile
    public createFile(event:any){
    this.addFile = event.target.files[0];
     
  }
// send a file to chosen client
  public onAddFileClient(addForm:NgForm,clientName:string):void{
    this.document.getElementById('cancelSendButton')?.click();
  
    if (this.addFile) {
  
     
  
      const formData = new FormData();
  
      formData.append("file", this.addFile);
      
      
  
      this.fileService.addFileByUser(formData,clientName, this.radioValue).subscribe(
          );
       
      
  }
  } 
 // store the value of the radio (encrypted or not when it changes)
  public radioChangeHandler(event:any) :void{
    this.radioValue=event.target.value;
  }
  //save the client to be deleted
  public deleteModel(client:Client):void{
    
    this.deleteClient =client;
  
    
  }
  //delete chosen client
  public onDeleteClient(clientName:string):void{
    this.document.getElementById("noClientButton")?.click();
    console.log("clientName" + clientName);
      this.clientService.deleteClient(clientName).subscribe(
        (Response: void)=>{
          this.getClients();
        }
      );
      
  }
  // add a client
  public onAddClient(form:NgForm):void{
    this.document.getElementById("clientCancelButton")?.click();
    this.clientService.addClient(this.clientname,this.password).subscribe(
      (Response: void)=>{
        this.getClients();
      }
    );
    
}
  


  
}


