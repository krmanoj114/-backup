import React from 'react';
import { Database, Download } from 'react-feather';

function Home() {
  return (
    <>
      <main className="container">
          <div className="bg-light p-3 mb-5 mt-2 rounded">
            <h1 className='text-danger '>R-OEM : </h1>
            {/* <img src={toyotaimg}  alt="" ></img> */}
            <p className="fs-6 fst-italic ">Some small description content come here related to the reom or anything brief related to project..</p>
          </div>
          <div className="row mb-2">
              <div className="col-md-6">
                <div className="row g-0 bg-light border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative">
                  <div className="col p-4 d-flex flex-column position-static">
                    <h3 className="mb-0 text-success">Code master &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;</h3>
                    <p className="card-text mb-auto">Code master ..</p>
                    <a href="/codemaster" className="stretched-link text-primary">Link</a>

                  </div>
                  <div className="col-auto">
                    <Database size={80} />
                  </div>
                </div>
              </div>
              <div className="col-md-6">
                <div className="row g-0 bg-light border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative">
                  <div className="col p-4 d-flex flex-column position-static">
                    <h3 className="mb-0 text-success">Common Upload / Download</h3>
                    <p className="card-text mb-auto">common upload & download ...</p>
                    <a href="/uploaddownload" className="stretched-link text-primary">Link</a>

                  </div>
                  <div className="col-auto">
                    <Download size={80} />
                  </div>
                </div>
              </div>
        </div>
      </main>
    </>
  )
}

export default Home;