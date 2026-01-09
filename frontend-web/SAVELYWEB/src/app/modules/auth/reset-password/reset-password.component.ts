import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-reset-password',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent {
  form: FormGroup;
  email: string = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirm: ['', Validators.required]
    });

    //
    this.route.queryParamMap.subscribe(params => {
      this.email = params.get('email') ?? '';
    });
  }

  submit() {
    if (this.form.invalid) return;
    if (this.form.value.password !== this.form.value.confirm) return;

    // TODO : appel backend
    this.router.navigate(['/login']);
  }
}
