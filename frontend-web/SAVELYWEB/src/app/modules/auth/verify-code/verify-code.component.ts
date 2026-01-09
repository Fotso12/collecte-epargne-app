import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-verify-code',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './verify-code.component.html',
  styleUrls: ['./verify-code.component.css']
})
export class VerifyCodeComponent {
  form: FormGroup;
  email: string = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      code: ['', Validators.required]
    });


    this.route.queryParamMap.subscribe(params => {
      this.email = params.get('email') ?? '';
    });
  }

  submit() {
    if (this.form.invalid) return;

    // TODO : appel backend pour vérifier le code

    this.router.navigate(['/reset-password'], {
      queryParams: { email: this.email }
    });
  }
}
