import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoomService {

  private baseUrl = 'http://localhost:8080/api/rooms';

  constructor(private http: HttpClient, private authService: AuthService) { }

  // Helper to add the Token to every request
  private getHeaders() {
    const token = this.authService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  // 1. Get All Rooms
  getAllRooms(): Observable<any> {
    return this.http.get(`${this.baseUrl}/all`, { headers: this.getHeaders() });
  }

  // 2. Add a New Room
  addRoom(roomData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/add`, roomData, { responseType: 'text', headers: this.getHeaders() });
  }
}