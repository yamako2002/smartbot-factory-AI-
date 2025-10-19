// src/app/core/auth.service.ts (ou src/app/auth.service.ts selon ton arbo)
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, tap } from 'rxjs';
import { environment } from '../../environments/environment'; // adapte le chemin si besoin

export interface LoginRequest { email: string; password: string; }
export interface RegisterRequest { fullName: string; email: string; password: string; }
export interface AuthResponse { token: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /** Login => stocke le token si présent, expose Observable<void> au composant */
  login(body: LoginRequest): Observable<void> {
    return this.http.post<AuthResponse>(`${this.base}/auth/login`, body).pipe(
      tap(res => {
        if (res?.token) {
          localStorage.setItem('jwt', res.token);
        }
      }),
      map(() => void 0)
    );
  }

  /**
   * Register => certains backends renvoient {token}, d'autres rien.
   * On gère les deux cas proprement.
   */
  register(body: RegisterRequest): Observable<void> {
    return this.http.post<Partial<AuthResponse> | void>(`${this.base}/auth/register`, body).pipe(
      tap((res: any) => {
        const token = res?.token;
        if (token) {
          localStorage.setItem('jwt', token);
        }
      }),
      map(() => void 0)
    );
  }

  logout(): void {
    localStorage.removeItem('jwt');
  }

  get token(): string | null {
    return localStorage.getItem('jwt');
  }

  isLoggedIn(): boolean {
    return !!this.token;
  }
}
