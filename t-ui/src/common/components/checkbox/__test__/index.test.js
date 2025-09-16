import { render, screen } from "@testing-library/react";
import Checkbox from "..";

describe('Checkbox Test Suite', () => {

  test('- should render Checkbox', () => {
    render(
      <Checkbox
        type="checkbox"
        name="checkbox"
        id="checkbox"
        handleClick={evt => console.log(evt)}
        isChecked={true}
      />
    );
    const checkbox = screen.getByTestId('tpex-checkbox');
    expect(checkbox).toBeTruthy();
  });
});