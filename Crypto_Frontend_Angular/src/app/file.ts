import * as uuid from 'uuid';

const myId = uuid.v4();
export interface File{
    user:string;
    uuid:string;
    fileName:string;
    dateOfCreation:string;
    
}