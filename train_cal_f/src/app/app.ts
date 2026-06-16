import { Component, signal } from '@angular/core';
import { single } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('Train Calculator');
  start_station = signal<string>('');
  destination_station = signal<string>('');
  staion_list = signal<string[]>(['volvo', 'Saab', 'opel', 'audi',]);

  onStartStationChange(event: Event) {
    const element = event.target as HTMLSelectElement;
    console.log(element.value);
  }
  onDestinationtStationChange(event: Event) {
    const element = event.target as HTMLSelectElement;
    console.log(element.value);
  }

  onCLick() {
    console.log("cal");

  }
}
