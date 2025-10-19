import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  loading = false;
  error: string | null = null;
  form!: FormGroup;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched(); // 👈 pour afficher les erreurs si vide
      return;
    }
    this.loading = true;
    this.error = null;

    this.auth.login(this.form.value as any).subscribe({
      next: () => this.router.navigateByUrl('/home'),
      error: (err) => {
        this.error = err?.error?.message || 'Authentification échouée';
        this.loading = false;
      }
    });
  }
}
