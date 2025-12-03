import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <--- Import this!
import { RoomService } from '../../services/room.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landlord-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule], // <--- Add FormsModule here
  templateUrl: './landlord-dashboard.html',
  styleUrls: ['./landlord-dashboard.css']
})
export class LandlordDashboardComponent implements OnInit {

  rooms: any[] = [];
  errorMessage = '';
  successMessage = '';

  // Data for the "Add Room" Form
  newRoom = {
    roomNo: '',
    floorNo: null,
    baseRent: null
  };

  showForm = false; // To toggle the form visibility

  constructor(private roomService: RoomService, private router: Router) {}

  ngOnInit() {
    this.fetchRooms();
  }

  fetchRooms() {
    this.roomService.getAllRooms().subscribe({
      next: (data) => this.rooms = data,
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Failed to load rooms.';
      }
    });
  }

  // Function to Add Room
  createRoom() {
    this.roomService.addRoom(this.newRoom).subscribe({
      next: (response) => {
        this.successMessage = 'Room Added Successfully!';
        this.fetchRooms(); // Refresh the list
        this.showForm = false; // Hide the form
        this.newRoom = { roomNo: '', floorNo: null, baseRent: null }; // Reset form
        
        // Hide success message after 3 seconds
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.errorMessage = 'Error adding room. Room Number might already exist.';
        console.error(err);
      }
    });
  }

  toggleForm() {
    this.showForm = !this.showForm;
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}