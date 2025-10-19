import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Chatbot {
  id: number;
  name: string;
  domain: string;
  status: 'READY' | 'INDEXING' | 'ERROR';
  sources: string[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateChatbotRequest {
  name: string;
  domain: string;
  sources: string[];
}

@Injectable({ providedIn: 'root' })
export class ChatbotService {
  private base = `${environment.apiUrl}/api/chatbots`;

  constructor(private http: HttpClient) {}

  list(): Observable<Chatbot[]> {
    return this.http.get<Chatbot[]>(this.base);
  }

  create(body: CreateChatbotRequest): Observable<Chatbot> {
    return this.http.post<Chatbot>(this.base, body);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
