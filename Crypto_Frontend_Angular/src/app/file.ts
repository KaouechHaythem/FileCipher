import * as uuid from 'uuid';

const myId = uuid.v4();
export interface File{
    uuid:string;
    fileName:string;
    dateOfCreation:string;
    
}