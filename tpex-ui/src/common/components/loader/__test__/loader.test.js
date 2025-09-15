import { render, screen } from '@testing-library/react';
import { TpexLoader } from '../loader';

describe('TpexLoader Test Suite', () => {

  test('- should render nothing, if nothing is supplied to TpexLoader', () => {
    render(
      <TpexLoader isLoading={false} />
    );
    expect(screen.queryByTestId('tpex-loader')).toBeNull();
  });

  test('should render nothing, if nothing is supplied to TpexLoader 2', () => {
    render(
      <TpexLoader />
    );
    expect(screen.queryByTestId('tpex-loader')).toBeNull();
  });

  test('- should render TpexLoader, if isLoading is true', () => {
    render(
      <TpexLoader isLoading={true} />
    );
    const tpexLoader = screen.getByTestId('tpex-loader');
    expect(tpexLoader).toBeTruthy();
  });
});
