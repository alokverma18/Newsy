import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators, FormArray, ReactiveFormsModule} from '@angular/forms';
import { SubscriptionService } from '../services/subscription.service';
import {CommonModule} from "@angular/common";
import {MatDialogModule, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-subscription',
  standalone: true,
  imports: [
    ReactiveFormsModule, CommonModule, MatDialogModule
  ],
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {
  form!: FormGroup;
  categories = ['sports','technology','business','entertainment','education'];
  message = '';
  submitting = false;

  constructor(
    private fb: FormBuilder,
    private svc: SubscriptionService,
    private dialogRef: MatDialogRef<SubscriptionComponent>
    ) {}

  ngOnInit() {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      categories: this.fb.array([], Validators.required)
    });
  }

  onCheckboxChange(e: any) {
    const arr: FormArray = this.form.get('categories') as FormArray;
    if (e.target.checked) arr.push(this.fb.control(e.target.value));
    else {
      const idx = arr.controls.findIndex(x => x.value === e.target.value);
      if (idx >= 0) arr.removeAt(idx);
    }
  }

  isSelected(cat: string): boolean {
    const arr = this.form.get('categories') as FormArray;
    return arr.value.includes(cat);
  }

  toggleCategory(cat: string) {
    const arr = this.form.get('categories') as FormArray;
    const idx = arr.value.indexOf(cat);

    if (idx >= 0) {
      arr.removeAt(idx);
    } else {
      arr.push(this.fb.control(cat));
    }
  }


  submit() {
    if (this.form.invalid) return;
    this.submitting = true;
    const payload = {
      email: this.form.value.email,
      categories: this.form.value.categories
    };
    this.svc.subscribe(payload).subscribe({
      next: (res) => {
        this.message = res?.message || 'Verification email sent. Check your inbox.';
        this.submitting = false;
        this.dialogRef.close();
      },
      error: (err) => {
        this.message = err?.error?.message || 'Error subscribing.';
        this.submitting = false;
      }
    });
  }
}
