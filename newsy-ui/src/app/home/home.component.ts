import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewsService } from '../services/news.service';
import { ThemeService } from '../services/theme.service';
import { NewsArticle, NewsResponse } from '../models/news.model';
import { LucideAngularModule, Bell, Newspaper, RefreshCw, Sun, Moon, Cpu, Trophy, Briefcase, Layers, AlertCircle, Inbox, ArrowRight, GraduationCap, Sparkles } from 'lucide-angular';
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {SubscriptionComponent} from "../subscription/subscription.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, MatDialogModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  // Lucide icons
  readonly Bell = Bell;
  readonly Newspaper = Newspaper;
  readonly RefreshCw = RefreshCw;
  readonly Sun = Sun;
  readonly Moon = Moon;
  readonly Cpu = Cpu;
  readonly Trophy = Trophy;
  readonly Briefcase = Briefcase;
  readonly Layers = Layers;
  readonly AlertCircle = AlertCircle;
  readonly Inbox = Inbox;
  readonly ArrowRight = ArrowRight;
  readonly GraduationCap = GraduationCap;
  readonly Sparkles = Sparkles;

  title = 'Newsy - Daily News Aggregator';
  newsData: NewsResponse | null = null;
  loading = false;
  error: string | null = null;
  selectedCategory: string = 'all';
  isDarkMode = false;

  constructor(
    private newsService: NewsService,
    public themeService: ThemeService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.loadNews();
    this.themeService.darkMode$.subscribe(isDark => {
      this.isDarkMode = isDark;
    });
  }

  loadNews() {
    this.loading = true;
    this.error = null;
    this.newsService.getAllNews().subscribe({
      next: (data) => {
        this.newsData = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load news. Please try again later.';
        this.loading = false;
        console.error('Error loading news:', err);
      }
    });
  }

  refreshNews() {
    this.loading = true;
    this.error = null;
    this.newsService.fetchNews().subscribe({
      next: () => {
        setTimeout(() => this.loadNews(), 2000);
      },
      error: (err) => {
        this.error = 'Failed to fetch latest news.';
        this.loading = false;
        console.error('Error fetching news:', err);
      }
    });
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }

  selectCategory(category: string) {
    this.selectedCategory = category;
  }

  getFilteredNews(): NewsArticle[] {
    if (!this.newsData) return [];

    if (this.selectedCategory === 'all') {
      return Object.values(this.newsData.news).flat();
    }

    const categoryKey = this.selectedCategory.charAt(0).toUpperCase() + this.selectedCategory.slice(1);
    return this.newsData.news[categoryKey] || [];
  }

  getDefaultImageForCategory(category: string): string {
    const defaultImages: { [key: string]: string } = {
      'technology': 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800',
      'sports': 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=800',
      'business': 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=800',
      'education': 'https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0?w=800',
      'entertainment': 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800'
    };
    return defaultImages[category.toLowerCase()] || 'https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=800';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    return date.toLocaleDateString();
  }

  openArticle(url: string) {
    window.open(url, '_blank');
  }

  openSubscriptionModal() {
      this.dialog.open(SubscriptionComponent, {
        width: '400px',
        minHeight: '300px',
        panelClass: 'newsy-dialog'
      });
  }
}
