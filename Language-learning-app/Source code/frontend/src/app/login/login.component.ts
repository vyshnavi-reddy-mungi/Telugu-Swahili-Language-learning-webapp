import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginDto } from '../models/LoginDto';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginDto:LoginDto = {};
  errorMessage: string = '';

  constructor(private router: Router, private apiService: ApiService) {

  }

  onSubmit() {
    this.apiService.postService(this.apiService.apiUrls.login, this.loginDto).toPromise().then(res => {
      if (res) {
        if (res.status === true) {
          localStorage.setItem('token', this.loginDto.username!);
          this.router.navigateByUrl("home");
        } else {
          this.errorMessage = res.responseMessage ? res.responseMessage : '';
        }
      }
    });
  }
}
