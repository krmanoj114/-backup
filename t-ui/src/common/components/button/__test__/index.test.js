import { render, screen } from '@testing-library/react';
import TpexSimpleButton from '..';

describe('TpexSimpleButton Test Suite', () => {

  test('- should render TpexSimpleButton', () => {
    render(
      <TpexSimpleButton
        color="btn btn-primary"
        text="Submit"
        handleClick={evt => console.log(evt)}
      />
    );
    const tpexSimpleButton = screen.getByTestId('tpex-simple-button');
    expect(tpexSimpleButton).toBeTruthy();
  });
});



