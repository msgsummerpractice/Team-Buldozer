export const getRoleColor = (role: string): string => {
  const colors: Record<string, string> = {
    ADMIN: 'var(--color-admin)',
    HR: 'var(--color-hr)',
    MARKETING: 'var(--color-marketing)',
    PARTICIPANT: 'var(--color-participant)',
  };
  return colors[role] ?? 'var(--color-default)';
};
