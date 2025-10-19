import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-recommendation',
  standalone: true, // ✅ needed since we use loadComponent()
  imports: [CommonModule, FormsModule],
  templateUrl: './recommendation.html',
  styleUrls: ['./recommendation.scss']
})
export class RecommendationPage {
  query: string = '';
  recommendations: any[] = [];
  loading = false;

  constructor(private http: HttpClient) {}

  getRecommendations() {
    if (!this.query.trim()) return;
    this.loading = true;

    const params = new HttpParams().set('query', this.query);
    this.http
      .get<any[]>('http://localhost:8080/api/recommend', { params })
      .subscribe({
        next: (res) => {
          this.recommendations = res;
          this.loading = false;
        },
        error: (err) => {
          console.error('Recommendation error:', err);
          this.loading = false;
        },
      });
  }
}
