import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  // THESE MUST MATCH YOUR FILE NAMES
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {

  loginData = {
    email: '',
    password: ''
  };
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    this.authService.login(this.loginData).subscribe({
      next: (token: string) => {
        this.authService.saveToken(token);
        alert('Login Successful!');
        // Redirect to landlord dashboard for now
        this.router.navigate(['/landlord-dashboard']);
      },
      error: (err) => {
        this.errorMessage = 'Invalid Email or Password';
        console.error(err);
      }
    });
  }
}