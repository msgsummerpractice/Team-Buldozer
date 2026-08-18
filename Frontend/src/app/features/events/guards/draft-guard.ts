import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { EventService } from '@features/events/services/event-service';
import { map } from 'rxjs';
import { EventResponse } from '@features/events/model/event-response';
import { EventStatusEnum } from '@features/events/model/event-status';

export const draftGuard: CanActivateFn = (route) => {
  const eventService = inject(EventService);
  const router = inject(Router);

  const stayHere = () => {
    const parentSegments = route.pathFromRoot
      .slice(0, -1)
      .flatMap((snapshot) => snapshot.url.map((segment) => segment.path))
      .filter((segment) => segment.length > 0);

    return router.createUrlTree(['/', ...parentSegments]);
  };

  const eventId = Number(route.paramMap.get('id'));

  if (!eventId || isNaN(eventId)) {
    return stayHere();
  }

  return eventService
    .getEventById(eventId)
    .pipe(
      map((event: EventResponse) =>
        event.status === EventStatusEnum.DRAFT ? true : stayHere()
      )
    );
};
