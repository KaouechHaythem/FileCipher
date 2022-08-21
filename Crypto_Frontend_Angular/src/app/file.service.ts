import { Injectable } from "@angular/core";
import { HttpClient } from '@angular/common/http';
import { File } from "./file";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
@Injectable({
    providedIn:'root'
})
export class FileService{
    private apiServerUrl=environment.apiBaseUrl;
    constructor(private http: HttpClient){}
    public getFiles():Observable<File[]>{
        return this.http.get<File[]>(`${this.apiServerUrl}/api/v1/file/findall`);
    }
    public getOneFile(fileId:string):Observable<File>{
        return this.http.get<File>(`${this.apiServerUrl}/api/v1/file/findone/${fileId}`);
    }
    public addFile(fileId:string, crypto:boolean):Observable<File>{
        return this.http.get<File>(`${this.apiServerUrl}/api/v1/file/miniodbupload/${crypto}`);
    }
    public deleteFile(fileId:string):Observable<void>{
        return this.http.delete<void>(`${this.apiServerUrl}/api/v1/file/findone/${fileId}`);
    }
}