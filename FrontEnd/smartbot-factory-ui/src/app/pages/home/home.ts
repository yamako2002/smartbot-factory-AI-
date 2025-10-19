import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';


interface Chatbot {
  id: number;
  name: string;
  domain: string;
  status: 'READY' | 'INDEXING' | 'ERROR';
  sources: string[];
  createdAt: string;
  updatedAt: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule,],
  templateUrl: './home.html',
  styleUrls: ['./home.scss'],
})
export class HomeComponent implements OnInit {
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);

  loading = false;
  creating = false;
  error: string | null = null;

  chatbots: Chatbot[] = [];
  filtered: Chatbot[] = [];

  showCreate = false;
  q = '';
  sort: 'recent' | 'name' | 'sources' = 'recent';

  createForm!: FormGroup;

  ngOnInit(): void {
    this.createForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      domain: ['', [Validators.required]],
      sourcesText: [''],
    });
    this.load();
  }
  

  private base = environment.apiUrl;
  

  private apiList() {
    return this.http.get<Chatbot[]>(`${this.base}/api/chatbots`);
  }
  private apiCreate(body: { name: string; domain: string; sources: string[] }) {
    return this.http.post<Chatbot>(`${this.base}/api/chatbots`, body);
  }
  private apiDelete(id: number) {
    return this.http.delete<void>(`${this.base}/api/chatbots/${id}`);
  }

  async load() {
    this.loading = true;
    this.error = null;
    try {
      const data = await firstValueFrom(this.apiList());
      this.chatbots = data ?? [];
      this.applyFilters();
    } catch (e: any) {
      this.error = e?.error?.message || 'Impossible de charger vos chatbots.';
    } finally {
      this.loading = false;
    }
  }

  openCreate() {
    this.showCreate = true;
  }

  closeCreate() {
    this.showCreate = false;
    // reset explicite pour éviter des undefined dans le template
    this.createForm.reset({ name: '', domain: '', sourcesText: '' });
    this.createForm.markAsPristine();
    this.createForm.markAsUntouched();
  }

  /** Création avec mise à jour immédiate de la liste */
  async create() {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }
    this.creating = true;
    this.error = null;

    // Normalisation des entrées
    const raw = this.createForm.value as { name?: string; domain?: string; sourcesText?: string };
    const name = (raw.name ?? '').trim();
    const domain = (raw.domain ?? '').trim();
    const sources = (raw.sourcesText ?? '')
      .split(/\r?\n/)
      .map((s: string) => s.trim())
      .filter((s: string) => s.length > 0);

    try {
      const bot = await firstValueFrom(this.apiCreate({ name, domain, sources }));
      // Optimistic add: nouvelle référence pour bien déclencher la détection de changement
      this.chatbots = [bot, ...(this.chatbots ?? [])];
      this.applyFilters();
      this.closeCreate();
    } catch (e: any) {
      this.error = e?.error?.message || 'Création échouée.';
      // En cas de doute, on pourrait re-synchroniser: await this.load();
    } finally {
      this.creating = false;
    }
  }

  /** Suppression avec update optimiste + revert si l’API échoue */
  async remove(id: number) {
    if (!confirm('Supprimer ce chatbot ?')) return;

    const previous = this.chatbots; // pour revert si erreur
    // Optimistic remove
    this.chatbots = (this.chatbots ?? []).filter(b => b.id !== id);
    this.applyFilters();

    try {
      await firstValueFrom(this.apiDelete(id));
    } catch (e: any) {
      // revert UI si l’API échoue
      this.chatbots = previous;
      this.applyFilters();
      alert(e?.error?.message || 'Suppression impossible.');
    }
  }

  applyFilters() {
    const query = this.q.trim().toLowerCase();
    let list = [...this.chatbots];

    if (query) {
      list = list.filter(b =>
        b.name.toLowerCase().includes(query) ||
        b.domain.toLowerCase().includes(query)
      );
    }

    switch (this.sort) {
      case 'name':
        list.sort((a, b) => a.name.localeCompare(b.name)); break;
      case 'sources':
        list.sort((a, b) => (b.sources?.length ?? 0) - (a.sources?.length ?? 0)); break;
      default:
        list.sort((a, b) => +new Date(b.updatedAt) - +new Date(a.updatedAt));
    }

    this.filtered = list;
  }
}
