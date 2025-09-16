import { render, screen } from "@testing-library/react";
import UplaodDownload from "../uploadDownload";

describe('Download Upload Test', () => {
    test('should be input file', () => {
        render(
            <UplaodDownload />
        );

        const downloadButton = screen.getByTestId('download-button');
        expect(downloadButton).toBeTruthy();

        const uploadButton = screen.getByTestId('upload-button');
        expect(uploadButton).toBeTruthy();
    });
});

