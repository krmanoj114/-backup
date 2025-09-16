import { render, screen } from '@testing-library/react';
import NotFound from '..';

describe('NotFound Page Test Suite', () => {

  test('NotFound page should render if page is not found', () => {
    render(
      <NotFound />
    );
    const pageNotFound = screen.getByText(/Page Not found../i);
    expect(pageNotFound).toBeTruthy();
  });
});