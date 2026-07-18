import { afterNextRender, ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PpssppRuntime } from './ppsspp-runtime';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppComponent {
  private readonly runtime = inject(PpssppRuntime);

  constructor() {
    afterNextRender(() => {
      void this.runtime.bootstrap().catch((error: unknown) => console.error(error));
    });
  }
}
