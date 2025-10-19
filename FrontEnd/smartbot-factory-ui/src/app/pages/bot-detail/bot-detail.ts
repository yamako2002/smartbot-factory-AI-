// src/app/pages/bot-detail/bot-detail.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { FormsModule } from '@angular/forms';

interface Chatbot {
  id: number;
  name: string;
  domain: string;
  status: 'READY' | 'INDEXING' | 'ERROR';
  sources: string[];
  createdAt: string;
  updatedAt: string;
}
interface ChatMessage {
  sender: 'user' | 'bot';
  text: string;
}


@Component({
  selector: 'app-bot-detail',
  standalone: true,
  imports: [CommonModule, RouterModule,FormsModule],
  templateUrl: './bot-detail.html',
  styleUrls: ['./bot-detail.scss'],
})
export class BotdetailComponent implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);

  base = environment.apiUrl;

  botId!: number;
  bot?: Chatbot;
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.botId = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load() {
    this.loading = true;
    this.error = null;
    this.http.get<Chatbot>(`${this.base}/api/chatbots/${this.botId}`).subscribe({
      next: (data) => {
        this.bot = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Chargement impossible';
        this.loading = false;
      },
    });
  }

  openImportText() {
  const text = prompt("Collez ici le texte à ingérer :");
  if (!text) return;

  this.http.post(
    `${this.base}/api/rag/${this.botId}/ingest-texts`,
    [text]
  ).subscribe({
    next: () => alert("Texte importé avec succès ✅"),
    error: (err) => alert(err?.error?.message ?? "Erreur d’importation")
  });
}

openImportFile() {
  const input = document.createElement("input");
  input.type = "file";
  input.accept = ".pdf,.txt,.doc,.docx"; // tu filtres les formats si tu veux
  input.onchange = () => {
    if (!input.files?.length) return;

    const formData = new FormData();
    formData.append("file", input.files[0]);

    this.http.post(
      `${this.base}/api/rag/${this.botId}/ingest-file`,
      formData
    ).subscribe({
      next: () => alert("✅ Fichier importé avec succès"),
      error: (err) => alert(err?.error?.message ?? "⚠️ Erreur d’importation")
    });
  };
  input.click();
}
messages: ChatMessage[] = [];
userInput: string = '';
sending = false;

sendMessage() {
  if (!this.userInput.trim()) return;

  const text = this.userInput.trim();

  // Ajouter le message de l’utilisateur
  this.messages.push({ sender: 'user', text });
  this.userInput = '';
  this.sending = true;

  this.http.post<{ answer: string }>(
    `${this.base}/api/rag/${this.botId}/ask`,
    { question: text }
  ).subscribe({
    next: (res) => {
      this.messages.push({ sender: 'bot', text: res.answer });
      this.sending = false;
    },
    error: (err) => {
      this.messages.push({
        sender: 'bot',
        text: err?.error?.message ?? "⚠️ Erreur lors de la réponse"
      });
      this.sending = false;
    }
  });
}

}
