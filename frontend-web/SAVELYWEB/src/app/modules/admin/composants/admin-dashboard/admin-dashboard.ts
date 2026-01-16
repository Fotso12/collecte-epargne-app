import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css'
})
export class AdminDashboard implements OnInit {
  stats: any = {};
  isLoading = true;

  constructor(private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.isLoading = false;
  }
}
