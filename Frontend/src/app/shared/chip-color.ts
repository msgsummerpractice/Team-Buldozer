import { UserRole, UserRoleEnum } from '@core/users/model/user-role';
import { EventStatus, EventStatusEnum } from '@features/events/model/event-status';
import { EventType, EventTypeEnum } from '@features/events/model/event-type';
import { EventLocation, EventLocationEnum } from '@features/events/model/event-location';

export const getChipColor = (
  enumKey: UserRole | UserRoleEnum | EventStatus | EventStatusEnum
): string => {
  const colors: Record<UserRoleEnum | EventStatusEnum, string> = {
    ADMIN: 'var(--color-msg-orange)',
    HR: 'var(--color-msg-purple)',
    MARKETING: 'var(--color-msg-green)',
    PARTICIPANT: 'var(--color-msg-blue)',
    PUBLISHED: 'var(--color-msg-blue)',
    DRAFT: 'var(--color-msg-orange)',
    COMPLETED: 'var(--color-msg-green)',
  };
  return colors[enumKey] ?? 'var(--color-default)';
};

export const getTypeChipColor = (type: EventType | EventTypeEnum): string => {
  const colors: Record<EventTypeEnum, string> = {
    INTERNAL: 'var(--color-default)',
    EXTERNAL: 'var(--color-default)',
    LOCAL: 'var(--color-default)',
  };
  return colors[type] ?? 'var(--color-default)';
};

export const getTypeIcon = (type: EventType | EventTypeEnum): string => {
  const icons: Record<EventTypeEnum, string> = {
    INTERNAL: 'corporate_fare',
    EXTERNAL: 'public',
    LOCAL: 'location_city',
  };
  return icons[type] ?? 'category';
};

export const getLocationChipColor = (location: EventLocation | EventLocationEnum): string => {
  const colors: Record<EventLocationEnum, string> = {
    CLUJ: 'var(--color-default)',
    TIMISOARA: 'var(--color-default)',
    MURES: 'var(--color-default)',
    ALL: 'var(--color-default)',
  };
  return colors[location] ?? 'var(--color-default)';
};

export const withAlpha = (color: string, percent = 75): string =>
  `color-mix(in srgb, ${color} ${percent}%, transparent)`;
