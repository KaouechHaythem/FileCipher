import { Injectable } from "@angular/core";
import { HttpClient } from '@angular/common/http';
import { MyFile } from "./file";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
@Injectable({
    providedIn:'root'
})
export class FileService{
    private apiServerUrl=environment.apiBaseUrl;
    constructor(private http: HttpClient){}
    public getFiles():Observable<MyFile[]>{
        return this.http.get<MyFile[]>(`${this.apiServerUrl}/api/v1/file/findall`);
    }
    public getOneFile(fileId:string):Observable<MyFile>{
        return this.http.get<MyFile>(`${this.apiServerUrl}/api/v1/file/findone/${fileId}`);
    }
    public getByClient(clientName:string):Observable<MyFile[]>{
        return this.http.get<MyFile[]>(`${this.apiServerUrl}/api/v1/file/findbyclient/${clientName}`);
    }
    public addFile(file:FormData, crypto:string):Observable<MyFile>{
        return this.http.post<MyFile>(`${this.apiServerUrl}/api/v1/file/miniodbupload/${crypto}`,file);
    }
    public deleteFile(fileId:string):Observable<void>{
        return this.http.delete<void>(`${this.apiServerUrl}/api/v1/file/delete/${fileId}`);
    }
    public downloadFile(fileName:string):Observable<void>{
        return this.http.get<void>(`${this.apiServerUrl}/api/v1/file/download/${fileName}`);
    }
    public finaldownloadFile(fileId:string,fileName:string) :Observable<Blob>{
        return this.http.get(`${this.apiServerUrl}/api/v1/file/download/${fileId}/${fileName}`,{responseType: 'blob'});
    }
    
    public addFileByUser(file:FormData,clientname:string,crypto:string):Observable<void>{
        return this.http.post<void>(`${this.apiServerUrl}/api/v1/file/finalupload/${clientname}/${crypto}`,file);
    }
    public clearDownloadFolder(filename:string):Observable<void>{
        return this.http.delete<void>(`${this.apiServerUrl}/api/v1/file/clear/${filename}`);
    }
 
}