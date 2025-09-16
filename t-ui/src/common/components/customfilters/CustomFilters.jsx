import { useState} from 'react'

export const CustomFilters = ({ columns}) => {
  const [filters, setFilters] = useState({})

  const handleSearch = (name, id) => {

    if (name) {
      setFilters((prevFilters) => ({
        ...prevFilters,
        [id]: name,
      }))
    } else {
      setFilters((prevFilters) => {
        const updatedFilters = { ...prevFilters }
        delete updatedFilters[id]

        return updatedFilters
      })
    }

    console.log("filters >> ", filters);
  }




  return (
    <>
      <table className="table table-success table-striped">
        <thead>
          <tr key="grid-sort-asc-dsc">
           
            {columns.map((column) => {
    
              return (

                <th key={column.id}>
                  <span>{column.name}</span>
                </th>
              )
            })}
          </tr>
          <tr>
            



            {columns.map(column => {
              return (
                <th key={`${column.id}-filter`}>
                  <input
                    key={`${column.name}-search`}
                    type="text"
                    className="form-control mt-1 mb-1"
                    placeholder={column.name}
                    value={filters[column.id] || ''}
                    onChange={event => handleSearch(event.target.value, column.id)}
                  />
                </th>
              )
            })}
          </tr>
        </thead>
        <tbody>
     
        </tbody>
      </table>

     
    </>
  )
}
