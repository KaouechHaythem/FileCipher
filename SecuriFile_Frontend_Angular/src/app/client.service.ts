import { Injectable } from "@angular/core";
import { HttpClient } from '@angular/common/http';

import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { Client } from "./client";
@Injectable({
    providedIn:'root'
})
export class ClientService{
    private apiServerUrl=environment.apiBaseUrl;
    constructor(private http: HttpClient){}
    public getClients():Observable<Client[]>{
        return this.http.get<Client[]>(`${this.apiServerUrl}/api/v1/client/findall`);
    }
    public addClient(clientname:string,password:string):Observable<void>{
        return this.http.get<void>(`${this.apiServerUrl}/api/v1/client/addclient/${clientname}/${password}`);
    }
    public deleteClient(clientname:string):Observable<void>{
        return this.http.delete<void>(`${this.apiServerUrl}/api/v1/client/delete/${clientname}`);
    }
   
}